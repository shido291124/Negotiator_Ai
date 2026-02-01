from app.database import save_meeting
from app.notifications.slack_notifier import send_slack_message
from app.notifications.reminder_scheduler import schedule_reminder
from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from app.storage.memory_db import USERS, MEETINGS
from app.ai.negotiator import negotiate
from app.orchestrator.orchestrator_client import OrchestratorClient
import uuid

router = APIRouter()
orchestrator = OrchestratorClient()

class MeetingRequest(BaseModel):
    user_a: str
    user_b: str
    duration: int

@router.post("/schedule")
def schedule(req: MeetingRequest):
    if req.user_a not in USERS or req.user_b not in USERS:
        raise HTTPException(status_code=404, detail="User profile missing")

    context = {
        "user_a": USERS[req.user_a],
        "user_b": USERS[req.user_b],
        "duration": req.duration
    }

    # 🔥 CORE DECISION BY ORCHESTRATE
    decision = orchestrator.decide_meeting(context)

    # Fallback OR demo-safe path
    result = negotiate(
        USERS[req.user_a],
        USERS[req.user_b],
        req.duration
    )

    if result["status"] == "NO_OVERLAP":
        return {
            "status": "NO_OVERLAP",
            "message": "User A tried to schedule a meeting with you"
        }

    meeting_id = str(uuid.uuid4())
    MEETINGS[meeting_id] = result

    return {
        "status": "CONFIRMED",
        "meeting_id": meeting_id,
        "details": result
    }
