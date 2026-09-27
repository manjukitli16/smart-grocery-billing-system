from database import get_db
from models.product import Product


def _validated_values(form):
    name = form.get("name", "").strip()
    category = form.get("category", "").strip()
    try:
        price = round(float(form.get("price", "")), 2)
        stock = int(form.get("stock", ""))
    except (TypeError, ValueError):
        raise ValueError("Enter a valid price and whole-number stock quantity.")
    if not name:
        raise ValueError("Product name is required.")
    if price < 0 or stock < 0:
        raise ValueError("Price and stock cannot be negative.")
    return name, category, price, stock


def list_products(in_stock=False):
    query = "SELECT * FROM products"
    if in_stock:
        query += " WHERE stock > 0"
    query += " ORDER BY name COLLATE NOCASE"
    return [Product.from_row(row) for row in get_db().execute(query).fetchall()]


def get_product(product_id):
    row = get_db().execute("SELECT * FROM products WHERE id = ?", (product_id,)).fetchone()
    return Product.from_row(row) if row else None


def add_product(form):
    values = _validated_values(form)
    cursor = get_db().execute(
        "INSERT INTO products (name, category, price, stock) VALUES (?, ?, ?, ?)", values
    )
    get_db().commit()
    return cursor.lastrowid


def update_product(product_id, form):
    values = _validated_values(form)
    cursor = get_db().execute(
        "UPDATE products SET name = ?, category = ?, price = ?, stock = ? WHERE id = ?",
        (*values, product_id),
    )
    get_db().commit()
    return cursor.rowcount > 0


def delete_product(product_id):
    try:
        cursor = get_db().execute("DELETE FROM products WHERE id = ?", (product_id,))
        get_db().commit()
        return cursor.rowcount > 0
    except Exception:
        get_db().rollback()
        raise