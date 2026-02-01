import os
import requests

WATSONX_API_KEY = os.getenv("OP7Mvf0PaN8dYsIC1CwEjrmhEmioHvu5IKrYaYUceo4R")

ORCHESTRATE_URL = "https://api.us-south.watsonx.ibm.com/orchestrate/decide"

class OrchestratorClient:
    """
    This client represents watsonx Orchestrate.
    It is the CORE DECISION MAKER.
    """

    def decide_meeting(self, context: dict) -> dict:
        if not WATSONX_API_KEY:
            raise RuntimeError("OP7Mvf0PaN8dYsIC1CwEjrmhEmioHvu5IKrYaYUceo4R")

        headers = {
            "Authorization": f"Bearer {WATSONX_API_KEY}",
            "Content-Type": "application/json"
        }

        payload = {
            "intent": "schedule_meeting",
            "context": context
        }

        # 🔹 In hackathon demos, this can be mocked
        # 🔹 Architecture is what matters
        response = requests.post(
            ORCHESTRATE_URL,
            headers=headers,
            json=payload,
            timeout=10
        )

        # If Orchestrate is mocked / unavailable
        if response.status_code != 200:
            return {
                "decision": "FALLBACK",
                "reason": "orchestrate_unavailable"
            }

        return response.json()
