USERS = {}
MEETINGS = {}
# Simple in-memory DB for hackathon
USER_DB = {}

def save_user_profile(slack_id: str, preferences: dict):
    USER_DB[slack_id] = preferences

def get_user_profile(slack_id: str):
    return USER_DB.get(slack_id)
