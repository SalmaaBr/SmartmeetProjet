// src/app/models/resource.model.ts
export interface Resource {
  idResource?: number;  // optionnel lors de la création
  name: string;
  typeResource: string;
  typeResourceStatus: string;

  // Maintenance-related fields
  maintenanceEnabled?: boolean;
  maintenancePeriodMonths?: number;
  maintenanceDurationDays?: number;
  initialMaintenanceDate?: string;
  nextMaintenanceDate?: string;
}
