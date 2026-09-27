from flask import Blueprint, render_template

from services.analytics_service import sales_history


sales_bp = Blueprint("sales", __name__)


@sales_bp.get("/sales")
def index():
    return render_template("sales.html", bills=sales_history())