from dataclasses import dataclass


@dataclass(frozen=True)
class Bill:
    id: int
    customer: str
    total: float
    created_at: str

    @classmethod
    def from_row(cls, row):
        return cls(**dict(row))