import os
from pathlib import Path


BASE_DIR = Path(__file__).resolve().parent


class Config:
    SECRET_KEY = os.environ.get("FRESHMART_SECRET_KEY", "local-development-key")
    DATABASE = os.environ.get(
        "FRESHMART_DATABASE", str(BASE_DIR / "database" / "freshmart.db")
    )
    DEBUG = os.environ.get("FLASK_DEBUG", "0") == "1"
    LOW_STOCK_THRESHOLD = 5