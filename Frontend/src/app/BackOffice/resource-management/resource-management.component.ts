import { Component, OnInit } from '@angular/core';
import { ResourceService } from '../../services/resource.service';
import { Resource } from '../../models/resource.model';
import { TypeResource, TypeResourceStatus } from '../../models/resource.enums';

@Component({
  selector: 'app-resource-management',
  templateUrl: './resource-management.component.html',
  styleUrls: ['./resource-management.component.css']
})
export class ResourceManagementComponent implements OnInit {
  newResource: Resource = {
    idResource: 0,
    name: '',
    typeResource: TypeResource.MICROPHONE,
    typeResourceStatus: TypeResourceStatus.AVAILABLE
  };

  resources: Resource[] = [];
  editingResourceId: number | null = null;
  resourceTypes = Object.values(TypeResource);
  resourceStatuses = Object.values(TypeResourceStatus);

  constructor(private resourceService: ResourceService) {}

  ngOnInit(): void {
    this.loadResources();
  }

  loadResources(): void {
    this.resourceService.getAllResources().subscribe(
      (data) => this.resources = data,
      (error) => console.error('Error loading resources:', error)
    );
  }

  addResource(): void {
    this.resourceService.createResource(this.newResource).subscribe(
      () => {
        this.loadResources();
        this.resetForm();
      },
      (error) => console.error('Error adding resource:', error)
    );
  }

  startEdit(resource: Resource): void {
    if (resource.idResource)
    this.editingResourceId = resource.idResource;
    this.newResource = { ...resource };
  }

  updateResource(): void {
    if (this.editingResourceId === null) return;

    this.resourceService.updateResource(this.editingResourceId, this.newResource).subscribe(
      () => {
        this.loadResources();
        this.resetForm();
      },
      (error) => console.error('Error updating resource:', error)
    );
  }

  deleteResource(id: number): void {
    if (confirm('Are you sure you want to delete this resource?')) {
      this.resourceService.deleteResource(id).subscribe(
        () => this.loadResources(),
        (error) => console.error('Error deleting resource:', error)
      );
    }
  }

  cancelEdit(): void {
    this.resetForm();
  }

  private resetForm(): void {
    this.editingResourceId = null;
    this.newResource = {
      idResource: 0,
      name: '',
      typeResource: TypeResource.MICROPHONE,
      typeResourceStatus: TypeResourceStatus.AVAILABLE
    };
  }
}
