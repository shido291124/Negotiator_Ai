from fastapi import APIRouter

router = APIRouter()

@router.post("/login")
def slack_login():
    return {
        "message": "Slack login successful (mock)",
        "slack_id": "U123456"
    }
