from flask import Blueprint, render_template

from services.analytics_service import dashboard_metrics


dashboard_bp = Blueprint("dashboard", __name__)


@dashboard_bp.get("/")
def index():
    return render_template("dashboard.html", **dashboard_metrics())