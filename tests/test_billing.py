import pytest

from app import create_app
from database import get_db


@pytest.fixture
def client(tmp_path):
    app = create_app({"TESTING": True, "DATABASE": str(tmp_path / "billing.db")})
    with app.test_client() as test_client:
        yield test_client


def test_checkout_records_sale_and_decrements_stock(client):
    client.post(
        "/inventory/add",
        data={"name": "Milk", "category": "Dairy", "price": "3.49", "stock": "5"},
    )
    with client.application.app_context():
        product_id = get_db().execute("SELECT id FROM products").fetchone()[0]

    response = client.post(
        "/billing",
        data={"product_id": [str(product_id)], "quantity": ["2"], "customer": "Alex"},
        follow_redirects=True,
    )
    assert response.status_code == 200
    assert "₹6.98".encode() in response.data
    assert b"Alex" in response.data

    with client.application.app_context():
        product = get_db().execute("SELECT stock FROM products WHERE id = ?", (product_id,)).fetchone()
        assert product["stock"] == 3
        assert get_db().execute("SELECT COUNT(*) FROM bills").fetchone()[0] == 1


def test_insufficient_stock_does_not_create_bill(client):
    client.post(
        "/inventory/add",
        data={"name": "Eggs", "category": "Dairy", "price": "4.00", "stock": "1"},
    )
    with client.application.app_context():
        product_id = get_db().execute("SELECT id FROM products").fetchone()[0]

    response = client.post(
        "/billing",
        data={"product_id": [str(product_id)], "quantity": ["2"]},
    )
    assert response.status_code == 200
    assert b"Not enough stock for Eggs" in response.data
    with client.application.app_context():
        assert get_db().execute("SELECT COUNT(*) FROM bills").fetchone()[0] == 0