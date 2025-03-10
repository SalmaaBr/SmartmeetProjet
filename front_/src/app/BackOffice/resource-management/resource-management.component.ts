import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ResourceService } from '../../services/resource.service';
import { Resource } from '../../models/resource.model';
import { TypeResource, TypeResourceStatus } from '../../models/resource.enums';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-resource-management',
  templateUrl: './resource-management.component.html',
  styleUrls: ['./resource-management.component.css']
})
export class ResourceManagementComponent implements OnInit {
  resourceForm!: FormGroup;
  resources: Resource[] = [];
  editingResourceId: number | null = null;
  resourceTypes = Object.values(TypeResource);
  resourceStatuses = Object.values(TypeResourceStatus);

  constructor(
    private resourceService: ResourceService,
    private fb: FormBuilder,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadResources();
  }

  initForm(): void {
    this.resourceForm = this.fb.group({
      idResource: [0],
      name: ['', Validators.required],
      typeResource: [TypeResource.MICROPHONE, Validators.required],
      typeResourceStatus: [TypeResourceStatus.AVAILABLE, Validators.required]
    });
  }

  loadResources(): void {
    this.resourceService.getAllResources().subscribe(
      (data) => {
        this.resources = data;
        // this.toastr.success('Resources loaded successfully!', 'Success');
      },
      (error) => {
        console.error('Error loading resources:', error);
        this.toastr.error('Failed to load resources.', 'Error');
      }
    );
  }

  addResource(): void {
    if (this.resourceForm.invalid) return;
    this.resourceService.createResource(this.resourceForm.value).subscribe(
      () => {
        this.loadResources();
        this.resetForm();
        this.toastr.success('Resource added successfully!', 'Success');
      },
      (error) => {
        console.error('Error adding resource:', error);
        this.toastr.error('Failed to add resource.', 'Error');
      }
    );
  }

  startEdit(resource: Resource): void {
    if (resource.idResource) {
      this.editingResourceId = resource.idResource;
      this.resourceForm.patchValue(resource);
    }
  }

  updateResource(): void {
    if (this.editingResourceId === null || this.resourceForm.invalid) return;

    this.resourceService.updateResource(this.editingResourceId, this.resourceForm.value).subscribe(
      () => {
        this.loadResources();
        this.resetForm();
        this.toastr.success('Resource updated successfully!', 'Success');
      },
      (error) => {
        console.error('Error updating resource:', error);
        this.toastr.error('Failed to update resource.', 'Error');
      }
    );
  }

  deleteResource(id: number): void {
    if (confirm('Are you sure you want to delete this resource?')) {
      this.resourceService.deleteResource(id).subscribe(
        () => {
          this.loadResources();
          this.toastr.warning('Resource deleted successfully!', 'Deleted');
        },
        (error) => {
          console.error('Error deleting resource:', error);
          this.toastr.error('Failed to delete resource.', 'Error');
        }
      );
    }
  }

  cancelEdit(): void {
    this.resetForm();
  }

  private resetForm(): void {
    this.editingResourceId = null;
    this.resourceForm.reset({
      idResource: 0,
      name: '',
      typeResource: TypeResource.MICROPHONE,
      typeResourceStatus: TypeResourceStatus.AVAILABLE
    });
  }
}
