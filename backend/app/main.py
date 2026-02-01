from fastapi import FastAPI
from app.users.users import router as users_router

app = FastAPI(title="Negotiator AI")

@app.get("/")
def health():
    return {"status": "running"}

app.include_router(users_router, prefix="/users", tags=["Users"])
