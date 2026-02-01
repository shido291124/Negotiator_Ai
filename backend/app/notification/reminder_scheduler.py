import threading
import time
from datetime import datetime

from app.notifications.slack_notifier import send_slack_message


def schedule_reminder(slack_user_id: str, meeting_time_iso: str):
    """
    Sends reminder 1 hour before meeting
    """

    meeting_time = datetime.fromisoformat(meeting_time_iso)
    reminder_time = meeting_time.timestamp() - 3600
    delay = reminder_time - time.time()

    if delay <= 0:
        return  # Too late for reminder

    def send():
        time.sleep(delay)
        send_slack_message(
            slack_user_id,
            "⏰ Reminder: You have a meeting in 1 hour"
        )

    threading.Thread(target=send, daemon=True).start()
