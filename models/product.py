from dataclasses import dataclass


@dataclass(frozen=True)
class Product:
    id: int
    name: str
    category: str
    price: float
    stock: int
    created_at: str

    @classmethod
    def from_row(cls, row):
        return cls(**dict(row))