from flask import Blueprint, abort, flash, redirect, render_template, request, url_for

from services.billing_service import create_bill, get_bill
from services.inventory_service import list_products


billing_bp = Blueprint("billing", __name__)


@billing_bp.route("/billing", methods=["GET", "POST"])
def checkout():
    products = list_products(in_stock=True)
    if request.method == "POST":
        product_ids = request.form.getlist("product_id")
        quantities = request.form.getlist("quantity")
        try:
            bill_id = create_bill(
                zip(product_ids, quantities), request.form.get("customer", "")
            )
        except ValueError as error:
            flash(str(error), "error")
        else:
            return redirect(url_for("billing.receipt", bill_id=bill_id))
    return render_template("billing.html", products=products)


@billing_bp.get("/billing/<int:bill_id>")
def receipt(bill_id):
    bill = get_bill(bill_id)
    if bill is None:
        abort(404)
    return render_template("bill.html", bill=bill)