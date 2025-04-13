import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AllTemplateFrontComponent } from "./FrontOffice/all-template-front/all-template-front.component";
import { AllTemplateBackComponent } from "./BackOffice/all-template-back/all-template-back.component";
import { HomeFrontComponent } from './FrontOffice/home-front/home-front.component';
import { AboutFrontComponent } from './FrontOffice/about-front/about-front.component';
import { ServiceFrontComponent } from './FrontOffice/service-front/service-front.component';
import { PortfolioComponent } from './FrontOffice/portfolio/portfolio.component';
import { ContactComponent } from './FrontOffice/contact/contact.component';
import { AnnouncementComponent } from './announcement/announcement.component';
import { SponsorComponent } from './sponsor/sponsor.component';
import { AllTemplateUserComponent } from './User/all-template-user/all-template-user.component';
import { RegisterComponent } from './User/register/register.component';
import { LoginComponent } from './User/login/login.component';
import { PasswordComponent } from './User/password/password.component';
import { ResourceManagementComponent } from "./BackOffice/resource-management/resource-management.component";
import { ResourceReservationManagementComponent } from "./BackOffice/resource-reservation-management/resource-reservation-management.component";
import { AuthGuard } from './auth/auth.guard';
import { CreateeventComponent } from './event/createevent/createevent/createevent.component';
import { GeteventComponent } from './event/getevent/getevent/getevent.component';
import { EditEventComponent } from './event/edit-event/edit-event.component';
import { CreateRecutementComponent } from './recutement/create-recutement/create-recutement.component';
import { EditRecruitmentComponent } from './recutement/edit-recruitment/edit-recruitment.component';
import { CreateSponsorComponent } from './sponsor/create-sponsor/create-sponsor.component';
import { EditSponsorComponent } from './sponsor/edit-sponsor/edit-sponsor.component';
import { CreateAnnancementComponent } from './announcement/create-annancement/create-annancement.component';

const routes: Routes = [
  {
    path: 'front',
    component: AllTemplateFrontComponent,
    children: [
      { path: '', component: HomeFrontComponent },   // Home visible par défaut
      { path: 'about', component: AboutFrontComponent }, // About visible quand on va sur /about
      { path: 'service', component: ServiceFrontComponent },
      { path: 'portfolio', component: PortfolioComponent },
      { path: 'contact', component: ContactComponent },
      { path: 'create-annancement', component: CreateAnnancementComponent },
      
    ]
  },

  {
    path: 'admin',
    component: AllTemplateBackComponent,
    canActivate: [AuthGuard],  // Protecting the main route
    canActivateChild: [AuthGuard], // Protecting child routes
    children: [
      { path: 'resources', component: ResourceManagementComponent },
      { path: 'resource-reservations', component: ResourceReservationManagementComponent },
      { path: '', redirectTo: 'resources', pathMatch: 'full' },
      { path: 'events', component: GeteventComponent },
      { path: 'create-event', component: CreateeventComponent },
      { path: 'create-sponsor', component: CreateSponsorComponent },
      { path: 'edit-event/:id', component: EditEventComponent },
      { path: 'edit-sponsor/:id', component: EditSponsorComponent },
      { path: 'create-recuitement', component: CreateRecutementComponent },
      { path: 'edit-recruitment/:id', component: EditRecruitmentComponent },
      { path: 'sponsors', component: SponsorComponent }, 
      { path: 'announcements', component: AnnouncementComponent }, // Add sponsor route
    ]
  },
  {
    path: 'register',
    component: RegisterComponent // Route indépendante pour RegisterComponent
  },

  {
    path: '', 
    redirectTo: '/login',  // Redirige vers la page de connexion par défaut
    pathMatch: 'full'      // Assure que l'URL vide redirige bien vers /login
  },
  { path: 'login', component: LoginComponent },
  { path: 'password', component: PasswordComponent },

  {
    path: 'user',
    component: AllTemplateUserComponent,
    children: [
      { path: 'register', component: RegisterComponent }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }