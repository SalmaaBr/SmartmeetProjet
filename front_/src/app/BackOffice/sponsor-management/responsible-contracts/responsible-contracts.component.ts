import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Contract, SponsorEventService } from '../../../services/sponsor-event.service';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { UserService } from '../../../services/user.service';

@Component({
  selector: 'app-responsible-contracts',
  templateUrl: './responsible-contracts.component.html',
  styleUrls: ['./responsible-contracts.component.css'],
  standalone: true,
  imports: [CommonModule, RouterModule]
})
export class ResponsibleContractsComponent implements OnInit {
  contracts: Contract[] = [];
  isLoading = false;
  currentUserId: number = 0;
  selectedContract: Contract | null = null;
  isSigning = false;
  signatureDataUrl: string | null = null;
  profileImageBlob: Blob | null = null;
  currentDate: Date = new Date();

  @ViewChild('signatureCanvas') signatureCanvas!: ElementRef<HTMLCanvasElement>;
  @ViewChild('videoElement') videoElement!: ElementRef<HTMLVideoElement>;
  @ViewChild('photoCanvas') photoCanvas!: ElementRef<HTMLCanvasElement>;
  @ViewChild('printableContent') printableContent!: ElementRef<HTMLDivElement>;

  private ctx: CanvasRenderingContext2D | null = null;
  private isDrawing = false;
  private videoStream: MediaStream | null = null;

  constructor(
    private sponsorEventService: SponsorEventService,
    private toastr: ToastrService,
    private router: Router,
    private http: HttpClient,
    private userService: UserService
  ) {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      try {
        const user = JSON.parse(userStr);
        if (typeof user.userID === 'number' && !isNaN(user.userID) && user.userID > 0) {
          this.currentUserId = user.userID;
        } else {
          this.handleInvalidUser();
        }
      } catch (e) {
        console.error('Error parsing user data:', e);
        this.handleInvalidUser();
      }
    } else {
      this.handleInvalidUser();
    }
  }

  private handleInvalidUser(): void {
    this.currentUserId = 0;
    this.toastr.error('Session expired or invalid. Please log in again.', 'Error');
    this.router.navigate(['/login']);
  }

  ngOnInit(): void {
    if (this.currentUserId === 0) {
      this.handleInvalidUser();
      return;
    }
    this.loadContracts();
    this.loadProfileImage();
  }

  loadContracts(): void {
    this.isLoading = true;
    this.sponsorEventService.getContractsByResponsibleUser(this.currentUserId).subscribe(
      (contracts) => {
        this.contracts = contracts;
        this.isLoading = false;
        if (contracts.length === 0) {
          this.toastr.info('No contracts found for your responsible sponsors.', 'Info');
        }
      },
      (error) => {
        console.error('Error loading contracts:', error);
        this.toastr.error('Failed to load contracts', 'Error');
        this.isLoading = false;
      }
    );
  }

  loadProfileImage(): void {
    if (this.currentUserId) {
      this.userService.getProfileImage(this.currentUserId).subscribe({
        next: (imageUrl: string) => {
          fetch(imageUrl)
            .then(response => response.blob())
            .then(blob => {
              this.profileImageBlob = blob;
            })
            .catch(err => {
              console.error('Erreur lors de la conversion de l\'URL en Blob:', err);
              this.toastr.error('Échec du traitement de la photo de profil', 'Erreur');
              this.profileImageBlob = null;
            });
        },
        error: (err) => {
          console.error('Erreur lors du chargement de la photo de profil:', err);
          this.toastr.error('Échec du chargement de la photo de profil', 'Erreur');
          this.profileImageBlob = null;
        }
      });
    }
  }

  viewContract(contract: Contract): void {
    this.selectedContract = contract;
    this.signatureDataUrl = contract.signature ?? null; // Charger la signature existante
  }

  closePreview(): void {
    this.selectedContract = null;
    this.signatureDataUrl = null;
  }

  startSigning(contract: Contract): void {
    this.selectedContract = contract;
    this.isSigning = true;
    setTimeout(() => {
      const canvas = this.signatureCanvas.nativeElement;
      this.ctx = canvas.getContext('2d');
      if (this.ctx) {
        this.ctx.strokeStyle = 'black';
        this.ctx.lineWidth = 2;
      }
      canvas.addEventListener('mousedown', this.startDrawing.bind(this));
      canvas.addEventListener('mousemove', this.draw.bind(this));
      canvas.addEventListener('mouseup', this.stopDrawing.bind(this));
      canvas.addEventListener('mouseout', this.stopDrawing.bind(this));
    }, 0);
  }

  private startDrawing(event: MouseEvent): void {
    this.isDrawing = true;
    const canvas = this.signatureCanvas.nativeElement;
    const rect = canvas.getBoundingClientRect();
    const x = event.clientX - rect.left;
    const y = event.clientY - rect.top;
    if (this.ctx) {
      this.ctx.beginPath();
      this.ctx.moveTo(x, y);
    }
  }

  private draw(event: MouseEvent): void {
    if (!this.isDrawing || !this.ctx) return;
    const canvas = this.signatureCanvas.nativeElement;
    const rect = canvas.getBoundingClientRect();
    const x = event.clientX - rect.left;
    const y = event.clientY - rect.top;
    this.ctx.lineTo(x, y);
    this.ctx.stroke();
  }

  private stopDrawing(): void {
    this.isDrawing = false;
  }

  clearSignature(): void {
    const canvas = this.signatureCanvas.nativeElement;
    if (this.ctx) {
      this.ctx.clearRect(0, 0, canvas.width, canvas.height);
    }
  }

  async validateSignature(): Promise<void> {
    if (!this.selectedContract) return;

    const canvas = this.signatureCanvas.nativeElement;
    const ctx = canvas.getContext('2d');
    if (ctx) {
      const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height);
      const data = imageData.data;
      let isEmpty = true;
      for (let i = 0; i < data.length; i += 4) {
        if (data[i + 3] !== 0) { // Vérifier si un pixel a une opacité non nulle
          isEmpty = false;
          break;
        }
      }
      if (isEmpty) {
        this.toastr.error('Please draw a signature before validating.', 'Error');
        return;
      }
    }

    try {
      this.videoStream = await navigator.mediaDevices.getUserMedia({ video: true });
      const video = this.videoElement.nativeElement;
      video.srcObject = this.videoStream;
      video.play();
      const modal = document.getElementById('webcamModal');
      if (modal) {
        modal.style.display = 'block';
      }
    } catch (error) {
      console.error('Error accessing webcam:', error);
      this.toastr.error('Failed to access webcam', 'Error');
    }
  }

  capturePhoto(): void {
    const video = this.videoElement.nativeElement;
    const canvas = this.photoCanvas.nativeElement;
    const ctx = canvas.getContext('2d');
    if (ctx) {
      ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
      canvas.toBlob((blob) => {
        if (blob && this.profileImageBlob) {
          const formData = new FormData();
          formData.append('webcamPhoto', blob, 'webcam.jpg');
          formData.append('profilePhoto', this.profileImageBlob, 'profile.jpg');
          this.verifyFace(formData);
        } else {
          this.toastr.error('Missing webcam photo or profile photo', 'Error');
          this.closeWebcamModal();
        }
      }, 'image/jpeg');
    }
  }

  verifyFace(formData: FormData): void {
    this.http.post('http://localhost:5000/verify-face', formData).subscribe(
      (response: any) => {
        if (response.isMatch) {
          this.toastr.success('Visage vérifié avec succès', 'Succès');
          this.attachSignatureToPreview();
        } else {
          this.toastr.error('Le visage ne correspond pas à la photo de profil', 'Erreur');
          this.closeWebcamModal();
        }
      },
      (error) => {
        console.error('Erreur lors de la vérification du visage:', error);
        const errorMessage = error.error?.error || 'Erreur inconnue lors de la vérification du visage';
        this.toastr.error(errorMessage, 'Erreur');
        this.closeWebcamModal();
      }
    );
  }

  attachSignatureToPreview(): void {
    const signatureCanvas = this.signatureCanvas.nativeElement;
    const tempCanvas = document.createElement('canvas');
    tempCanvas.width = 200; // Redimensionner pour limiter la taille
    tempCanvas.height = 100;
    const tempCtx = tempCanvas.getContext('2d');
    if (tempCtx) {
      tempCtx.drawImage(signatureCanvas, 0, 0, 200, 100);
      this.signatureDataUrl = tempCanvas.toDataURL('image/png');
    } else {
      this.signatureDataUrl = signatureCanvas.toDataURL('image/png');
    }
    this.closeWebcamModal();
    this.isSigning = false;

    // Mettre à jour le statut du contrat et envoyer la signature via l'API
    if (this.selectedContract) {
      const formData = new FormData();
      formData.append('contractId', this.selectedContract.id.toString());
      formData.append('status', 'APPROVED');
      formData.append('signature', this.signatureDataUrl); // Envoyer la signature

      this.http.post(`${this.sponsorEventService['apiUrl']}/update-contract-status`, formData).subscribe(
        () => {
          this.toastr.success('Contrat signé et validé avec succès', 'Succès');
          this.loadContracts(); // Recharger les contrats pour mettre à jour les données
        },
        (error) => {
          console.error('Erreur lors de la mise à jour du statut du contrat:', error);
          this.toastr.error('Échec de la mise à jour du statut du contrat', 'Erreur');
        }
      );
    }
  }

  closeWebcamModal(): void {
    const modal = document.getElementById('webcamModal');
    if (modal) {
      modal.style.display = 'none';
    }
    if (this.videoStream) {
      this.videoStream.getTracks().forEach(track => track.stop());
      this.videoStream = null;
    }
  }

  downloadContract(contract: Contract): void {
    this.sponsorEventService.getContractFile(contract.id).subscribe(
      (blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `contract_${contract.sponsor?.nom}_${contract.event?.title}.pdf`;
        link.click();
        window.URL.revokeObjectURL(url);
      },
      (error) => {
        console.error('Error downloading contract:', error);
        this.toastr.error('Failed to download contract', 'Error');
      }
    );
  }

  downloadPDF(): void {
    if (!this.selectedContract) return;
    this.sponsorEventService.downloadPDF(this.selectedContract, this.signatureDataUrl);
  }

  getStatusClass(status: string): string {
    switch (status.toUpperCase()) {
      case 'PENDING': return 'badge bg-warning';
      case 'APPROVED': return 'badge bg-success';
      case 'REJECTED': return 'badge bg-danger';
      default: return 'badge bg-secondary';
    }
  }
}
