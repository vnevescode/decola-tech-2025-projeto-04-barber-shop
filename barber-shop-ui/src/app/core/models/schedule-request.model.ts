export interface ScheduleRequest {
  startAt: string; // ISO date string (ex: 2025-04-14T14:00:00Z)
  endAt: string;
  clientId: string; // UUID
  haircutTypeId: string; // UUID
}
