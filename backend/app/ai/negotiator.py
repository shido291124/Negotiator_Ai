def negotiate(user_a, user_b, duration):
    """
    Deterministic reasoning skill.
    Orchestrate CALLS this, not the other way around.
    """

    for a in user_a["availability"]:
        for b in user_b["availability"]:
            if a["day"] == b["day"]:
                start = max(a["start"], b["start"])
                end = min(a["end"], b["end"])

                if (end - start) >= duration:
                    return {
                        "status": "CONFIRMED",
                        "day": a["day"],
                        "start": start
                    }

    return {
        "status": "NO_OVERLAP"
    }
