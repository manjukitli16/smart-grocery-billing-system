from flask import Flask

from config import Config
from database import init_db
from database.db import register_db
from routes.billing import billing_bp
from routes.dashboard import dashboard_bp
from routes.inventory import inventory_bp
from routes.sales import sales_bp


def create_app(test_config=None):
	app = Flask(__name__)
	app.config.from_object(Config)
	if test_config:
		app.config.update(test_config)
	register_db(app)

	app.register_blueprint(dashboard_bp)
	app.register_blueprint(inventory_bp)
	app.register_blueprint(billing_bp)
	app.register_blueprint(sales_bp)

	with app.app_context():
		init_db()

	return app


app = create_app()


if __name__ == "__main__":
	app.run(debug=app.config["DEBUG"])
