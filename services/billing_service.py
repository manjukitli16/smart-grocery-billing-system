from database import get_db


def create_bill(items, customer=""):
    quantities_by_product = {}
    for product_id, quantity in items:
        try:
            product_id = int(product_id)
            quantity = int(quantity)
        except (TypeError, ValueError):
            raise ValueError("Choose a product and enter a whole-number quantity.")
        if quantity < 1:
            raise ValueError("Quantities must be at least one.")
        quantities_by_product[product_id] = quantities_by_product.get(product_id, 0) + quantity

    if not quantities_by_product:
        raise ValueError("Add at least one item to the bill.")

    db = get_db()
    try:
        db.execute("BEGIN IMMEDIATE")
        bill_lines = []
        total_paise = 0
        for product_id, quantity in quantities_by_product.items():
            product = db.execute(
                "SELECT id, name, price, stock FROM products WHERE id = ?", (product_id,)
            ).fetchone()
            if product is None:
                raise ValueError("One of the selected products no longer exists.")
            if product["stock"] < quantity:
                raise ValueError(f"Not enough stock for {product['name']}.")
            unit_price_paise = round(product["price"] * 100)
            line_total_paise = unit_price_paise * quantity
            total_paise += line_total_paise
            bill_lines.append((product, quantity, unit_price_paise, line_total_paise))

        customer_name = customer.strip() or "Walk-in customer"
        cursor = db.execute(
            "INSERT INTO bills (customer, total) VALUES (?, ?)",
            (customer_name, total_paise / 100),
        )
        bill_id = cursor.lastrowid
        for product, quantity, unit_price_paise, line_total_paise in bill_lines:
            db.execute(
                "INSERT INTO bill_items "
                "(bill_id, product_id, product_name, quantity, unit_price, line_total) "
                "VALUES (?, ?, ?, ?, ?, ?)",
                (
                    bill_id,
                    product["id"],
                    product["name"],
                    quantity,
                    unit_price_paise / 100,
                    line_total_paise / 100,
                ),
            )
            db.execute(
                "UPDATE products SET stock = stock - ? WHERE id = ?",
                (quantity, product["id"]),
            )
        db.commit()
        return bill_id
    except Exception:
        db.rollback()
        raise


def get_bill(bill_id):
    db = get_db()
    bill = db.execute("SELECT * FROM bills WHERE id = ?", (bill_id,)).fetchone()
    if bill is None:
        return None
    items = db.execute(
        "SELECT * FROM bill_items WHERE bill_id = ? ORDER BY id", (bill_id,)
    ).fetchall()
    return {"id": bill["id"], "customer": bill["customer"], "total": bill["total"],
            "created_at": bill["created_at"], "items": items}