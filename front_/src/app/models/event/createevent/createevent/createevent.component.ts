import { Component , ViewChild, ElementRef } from '@angular/core'; 
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { EventService } from '../../../../services/event.service';
import { TypeEvent, TypeTheme, TypeWeather } from './event.enums';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import * as L from 'leaflet';
import { MapService } from '../../../../services/map.service';



delete (L.Icon.Default.prototype as any)._getIconUrl;

L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon-2x.png',
  iconUrl: 'https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.7.1/dist/images/marker-shadow.png',
});

@Component({
  selector: 'app-createevent',
  templateUrl: './createevent.component.html',
  styleUrls: ['./createevent.component.css']
})
export class CreateeventComponent {
  form: FormGroup;
  typeevent = Object.values(TypeEvent);
  typetheme = Object.values(TypeTheme);
  typeweather = Object.values(TypeWeather);
  isFormSubmitted = false;  // Variable pour suivre la soumission du formulaire
  selectedFile: File | null = null;
  previewUrl: string | ArrayBuffer | null = null;

  // Map variables
  map: any;
  markers: L.Marker[] = [];
  searchResults: any[] = [];
  showSearchResults = false;
  selectedLocation: any = null;

  constructor(private fb: FormBuilder, private router: Router, private eventService: EventService, private toastr: ToastrService,private mapService: MapService) {
    this.form = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      location: ['', Validators.required],
      latitude: [''],
      longitude: [''],
      typeevent: [TypeEvent.CONCERT, Validators.required],
      typetheme: [TypeTheme.PROGRAMMING, Validators.required],
      typeweather: [TypeWeather.SUNNY, Validators.required],
      startTime: ['', Validators.required],
      endTime: ['', Validators.required],
      maxParticipants: [0, [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit(): void {
    this.initMap();
    
  }

  initMap(): void {
    this.map = L.map('map').setView([36.8065, 10.1815], 13); // Default to Tunis coordinates

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors'
    }).addTo(this.map);

    this.map.on('click', (e: L.LeafletMouseEvent) => {
      this.addMarker(e.latlng);
      this.form.patchValue({
        latitude: e.latlng.lat,
        longitude: e.latlng.lng
      });
      this.updateLocationName(e.latlng.lat, e.latlng.lng);
    });
  }



  
  addMarker(latlng: L.LatLng): void {
    this.markers.forEach(marker => this.map.removeLayer(marker));
    this.markers = [];
    
    // Utilise le marqueur par défaut avec une couleur rouge
    const marker = L.marker(latlng, {
      icon: new L.Icon.Default({
        className: 'red-marker' // Ajoute une classe CSS
      })
    }).addTo(this.map);
    
    this.markers.push(marker);
    this.map.setView(latlng, 13);
  }

  searchLocation(): void {
    const query = this.form.get('location')?.value;
    if (query && query.length > 2) {
      this.mapService.searchLocation(query).subscribe({
        next: (results: any) => {
          this.searchResults = JSON.parse(results);
          this.showSearchResults = this.searchResults.length > 0;
        },
        error: (err) => {
          console.error('Error searching location:', err);
        }
      });
    }
  }

  selectSearchResult(result: any): void {
    const lat = parseFloat(result.lat);
    const lon = parseFloat(result.lon);
    const latlng = L.latLng(lat, lon);
    
    this.addMarker(latlng);
    this.form.patchValue({
      location: result.display_name,
      latitude: lat,
      longitude: lon
    });
    
    this.selectedLocation = result;
    this.showSearchResults = false;
  }

  updateLocationName(lat: number, lng: number): void {
    this.mapService.reverseGeocode(lat, lng).subscribe({
      next: (result: any) => {
        const data = JSON.parse(result);
        if (data.display_name) {
          this.form.patchValue({
            location: data.display_name
          });
        }
      },
      error: (err) => {
        console.error('Error reverse geocoding:', err);
      }
    });
  }

  get dateError(): boolean {
    const start = this.form.value.startTime;
    const end = this.form.value.endTime;
    return start && end && new Date(start) >= new Date(end);
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    this.selectedFile = file;

    // Prévisualisation de l'image
    const reader = new FileReader();
    reader.onload = () => {
        this.previewUrl = reader.result;
    };
    reader.readAsDataURL(file);
}

  onSubmit() {  
    this.isFormSubmitted = true;  // Marquer le formulaire comme soumis

    if (this.dateError) {
      this.toastr.error('La date de fin doit être après la date de début.', 'Erreur');
    }

    if (!this.selectedFile) {
      this.toastr.error('Veuillez sélectionner une image.', 'Erreur');
      return;
    }

    if (this.form.valid && this.selectedFile && !this.dateError) {
      this.eventService.createEvent(this.form.value, this.selectedFile).subscribe(
        () => {
          this.toastr.success('Événement créé avec succès !', 'Succès');
          this.router.navigate(['/admin/events']);
        },
        (error) => {
          console.error('Erreur lors de la création de l\'événement', error);
          this.toastr.error('Erreur lors de la création de l\'événement', 'Erreur');
        }
      );
    } 

    else {
      this.toastr.error('Veuillez remplir tous les champs requis et sélectionner un fichier.', 'Erreur');
    }
  }
}