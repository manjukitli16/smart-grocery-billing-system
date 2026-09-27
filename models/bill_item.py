from dataclasses import dataclass


@dataclass(frozen=True)
class BillItem:
    id: int
    bill_id: int
    product_id: int | None
    product_name: str
    quantity: int
    unit_price: float
    line_total: float

    @classmethod
    def from_row(cls, row):
        return cls(**dict(row))