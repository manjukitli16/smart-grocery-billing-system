# Fresh Mart

Fresh Mart is a grocery inventory and point-of-sale app. The Java/JSP application is the primary version; the earlier Flask app remains available during the transition. Product prices, sales, and receipts use Indian rupees (INR).

## Java/JSP app

Install JDK 17 or newer and Maven 3.9 or newer, then run from the project root:

```powershell
mvn spring-boot:run
```

Open <http://localhost:8080>. SQLite is created automatically at
`database/freshmart.db`. Set `FRESHMART_DATABASE` to use a different database
file, or `PORT` to change the web port.

## Pages and features

- Dashboard with today's sales, product count, and low-stock alerts
- Inventory search, product creation, editing, and removal
- Checkout with a live rupee total, stock validation, and transactional stock updates
- Separate sales history and printable receipt pages
- Responsive navigation, form validation messages, and empty states

## Java tests

```powershell
mvn test
```

## Legacy Flask app

The original Python version remains at `app.py`. Run it with `py -m pip install -r requirements.txt` followed by `py app.py`; it serves on <http://127.0.0.1:5000>.