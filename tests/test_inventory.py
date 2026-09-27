import pytest

from app import create_app
from database import get_db


@pytest.fixture
def client(tmp_path):
    app = create_app({"TESTING": True, "DATABASE": str(tmp_path / "inventory.db")})
    with app.test_client() as test_client:
        yield test_client


def test_add_and_edit_product(client):
    response = client.post(
        "/inventory/add",
        data={"name": "Fuji apples", "category": "Produce", "price": "1.25", "stock": "12"},
        follow_redirects=True,
    )
    assert response.status_code == 200
    assert b"Fuji apples" in response.data

    with client.application.app_context():
        product_id = get_db().execute("SELECT id FROM products").fetchone()[0]

    response = client.post(
        f"/inventory/{product_id}/edit",
        data={"name": "Fuji apples", "category": "Fruit", "price": "1.50", "stock": "8"},
        follow_redirects=True,
    )
    assert response.status_code == 200
    assert "₹1.50".encode() in response.data


def test_rejects_negative_stock(client):
    response = client.post(
        "/inventory/add",
        data={"name": "Bad stock", "category": "Other", "price": "2", "stock": "-1"},
        follow_redirects=True,
    )
    assert b"Price and stock cannot be negative" in response.data