# Route Recording Policy

QuestWalk records routes as a user-facing quest result, not as a precision sports analysis trace.

## Policy

- Keep the original accepted route points in history.
- Do not snap routes to roads or paths without a separate privacy and API-cost decision.
- Prefer plausible walking traces over raw GPS noise.
- Remove low-quality and physically unlikely points before saving.
- Simplify only the displayed polyline so the result map is readable.

## Current Thresholds

- Location request: high accuracy, 2 second default interval, 5 m minimum update distance hint.
- Recording filter: reject accuracy worse than 25 m.
- Recording filter: reject movement below 5 m.
- Recording filter: reject jumps above a 6 m/s walking/running allowance plus GPS accuracy tolerance.
- Recording filter: smooth accepted GPS points with a local Kalman filter.
- Display filter: remove route points closer than 4 m.
- Display filter: simplify the polyline with a 4 m Ramer-Douglas-Peucker tolerance.

## Deferred

Road snapping or map matching should be a separate feature because it sends route data to an external API and can behave poorly for parks, alleys, indoor movement, or off-road paths.
