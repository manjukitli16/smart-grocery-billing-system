from database import get_db
from models.bill import Bill


def dashboard_metrics():
    db = get_db()
    today = db.execute(
        "SELECT COALESCE(SUM(total), 0) AS total, COUNT(*) AS count "
        "FROM bills WHERE date(created_at) = date('now', 'localtime')"
    ).fetchone()
    product_count = db.execute("SELECT COUNT(*) FROM products").fetchone()[0]
    low_stock = db.execute(
        "SELECT * FROM products WHERE stock <= ? ORDER BY stock, name COLLATE NOCASE",
        (5,),
    ).fetchall()
    recent = db.execute("SELECT * FROM bills ORDER BY id DESC LIMIT 6").fetchall()
    return {
        "today_total": today["total"],
        "today_count": today["count"],
        "product_count": product_count,
        "low_stock": low_stock,
        "recent_bills": [Bill.from_row(row) for row in recent],
    }


def sales_history():
    rows = get_db().execute("SELECT * FROM bills ORDER BY id DESC").fetchall()
    return [Bill.from_row(row) for row in rows]