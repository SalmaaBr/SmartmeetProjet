// src/app/services/resource-type-status.service.ts
import { Injectable } from '@angular/core';
import { ResourceType } from '../models/resource-type.model';
import { ResourceStatus } from '../models/resource-status.model';

@Injectable({
  providedIn: 'root'
})
export class ResourceTypeStatusService {

  private resourceTypes: ResourceType[] = [];
  private resourceStatuses: ResourceStatus[] = [];

  constructor() { }

  // Ajouter un type de ressource
  addResourceType(resourceType: ResourceType): void {
    this.resourceTypes.push(resourceType);
  }

  // Ajouter un statut de ressource
  addResourceStatus(resourceStatus: ResourceStatus): void {
    this.resourceStatuses.push(resourceStatus);
  }

  // Récupérer tous les types de ressources
  getResourceTypes(): ResourceType[] {
    return this.resourceTypes;
  }

  // Récupérer tous les statuts de ressources
  getResourceStatuses(): ResourceStatus[] {
    return this.resourceStatuses;
  }
}
