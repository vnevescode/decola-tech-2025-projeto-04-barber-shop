export interface ScheduleResponse {
  id: string;
  startAt: string;
  endAt: string;
  confirmed: boolean;
  canceled: boolean;
  clientName: string;
  haircutName: string;
}
