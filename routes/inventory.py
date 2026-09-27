import sqlite3

from flask import Blueprint, flash, redirect, render_template, request, url_for

from services.inventory_service import (
    add_product,
    delete_product,
    get_product,
    list_products,
    update_product,
)


inventory_bp = Blueprint("inventory", __name__)


@inventory_bp.get("/inventory")
def index():
    return render_template("inventory.html", products=list_products())


@inventory_bp.route("/inventory/add", methods=["GET", "POST"])
def add():
    if request.method == "POST":
        try:
            add_product(request.form)
        except (ValueError, sqlite3.IntegrityError) as error:
            flash(str(error) or "A product with that name already exists.", "error")
        else:
            flash("Product added to inventory.", "success")
            return redirect(url_for("inventory.index"))
    return render_template("add_product.html")


@inventory_bp.route("/inventory/<int:product_id>/edit", methods=["GET", "POST"])
def edit(product_id):
    product = get_product(product_id)
    if product is None:
        flash("Product not found.", "error")
        return redirect(url_for("inventory.index"))

    if request.method == "POST":
        try:
            update_product(product_id, request.form)
        except (ValueError, sqlite3.IntegrityError) as error:
            flash(str(error) or "A product with that name already exists.", "error")
        else:
            flash("Inventory updated.", "success")
            return redirect(url_for("inventory.index"))
    return render_template("edit_product.html", product=product)


@inventory_bp.post("/inventory/<int:product_id>/delete")
def delete(product_id):
    if delete_product(product_id):
        flash("Product removed.", "success")
    else:
        flash("This product is part of a sale and cannot be removed.", "error")
    return redirect(url_for("inventory.index"))