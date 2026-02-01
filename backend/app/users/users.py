from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import List, Optional

router = APIRouter()

# In-memory store (OK for hackathon)
USER_PROFILES = {}

# ---------- MODELS ----------

class Preferences(BaseModel):
    preferred_time_of_day: str        # morning | afternoon | evening | any
    avoided_days: List[str]           # ["Monday", "Sunday"]
    max_duration: int                 # minutes
    flexibility: str                  # strict | flexible | very_flexible

class UserProfile(BaseModel):
    slack_id: str
    availability: List[str] = []
    preferences: Preferences

# ---------- ROUTES ----------

@router.post("/profile")
def save_profile(profile: UserProfile):
    USER_PROFILES[profile.slack_id] = profile.dict()
    return {"status": "saved", "slack_id": profile.slack_id}

@router.get("/profile/{slack_id}")
def get_profile(slack_id: str):
    if slack_id not in USER_PROFILES:
        raise HTTPException(status_code=404, detail="User not found")
    return USER_PROFILES[slack_id]
