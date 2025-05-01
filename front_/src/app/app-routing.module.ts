import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {AllTemplateFrontComponent} from "./FrontOffice/all-template-front/all-template-front.component";
import {AllTemplateBackComponent} from "./BackOffice/all-template-back/all-template-back.component";
import { HomeFrontComponent } from './FrontOffice/home-front/home-front.component';
import { AboutFrontComponent } from './FrontOffice/about-front/about-front.component';
import { ServiceFrontComponent } from './FrontOffice/service-front/service-front.component';
import { PortfolioComponent } from './FrontOffice/portfolio/portfolio.component';
import { ContactComponent } from './FrontOffice/contact/contact.component';
import { AllTemplateUserComponent } from './User/all-template-user/all-template-user.component';
import { RegisterComponent } from './User/register/register.component';
import { LoginComponent } from './User/login/login.component';
import { PasswordComponent } from './User/password/password.component';
import { ResourceManagementComponent} from "./BackOffice/resource-management/resource-management.component";
import { ResourceReservationManagementComponent} from "./BackOffice/resource-reservation-management/resource-reservation-management.component";
import { ResourceMaintenanceComponent } from './BackOffice/resource-maintenance/resource-maintenance.component';
import { AuthGuard } from './auth/auth.guard';
import { ActivateAccountComponent } from './User/activate-account/activate-account.component';
import { ResetPasswordRequestComponent } from './User/reset-password-request/reset-password-request.component';
import { ResetPasswordComponent } from './User/reset-password/reset-password.component';
import { UserComponent } from './BackOffice/user/user.component';
import { ProfileComponent } from './BackOffice/profile/profile.component';
import { CreateeventComponent } from './models/event/createevent/createevent/createevent.component';
import { GeteventComponent } from './models/event/getevent/getevent/getevent.component';
import { EditEventComponent } from './models/event/edit-event/edit-event.component';
import { CreateRecutementComponent } from './models/recutement/create-recutement/create-recutement.component';
import {EditRecruitmentComponent } from './models/recutement/edit-recruitment/edit-recruitment.component';
import {AddFeedbackComponent} from "./BackOffice/add-feedback/add-feedback.component";
import { MentalHealthComponent } from './BackOffice/mental-health/mental-health.component';
import { ResourceReservationStatisticsComponent } from './BackOffice/resource-reservation-statistics/resource-reservation-statistics.component';
import { NotificationComponent } from './components/notification/notification.component';
import { MaintenanceNotificationAdminComponent } from './components/maintenance-notification-admin/maintenance-notification-admin.component';
import { DocumentManagementComponent } from './BackOffice/document-management/document-management.component';
import { MessageComposeComponent } from './FrontOffice/message-compose/message-compose.component';
import { MessageInboxComponent } from './FrontOffice/message-inbox/message-inbox.component';
import { MessageSentComponent } from './FrontOffice/message-sent/message-sent.component';
import {FeedbackListComponent} from "./BackOffice/feedback-list/feedback-list.component";
import {
  MentalHealthListComponent
} from "./BackOffice/mental-health-list/mental-health-list.component";
const routes: Routes = [
  {
    path: 'front',
    component: AllTemplateFrontComponent,
    children: [
      { path: '', component: HomeFrontComponent },   // Home visible par défaut
      { path: 'about', component: AboutFrontComponent }, // About visible quand on va sur /about
      {path: 'service', component:ServiceFrontComponent},
      {path: 'portfolio', component:PortfolioComponent},
      {path: 'contact', component:ContactComponent},
      { path: 'profile', component: ProfileComponent  },
      {
        path: 'notifications',
        component: NotificationComponent,
      },
   ]},
   {
    path: 'messages',
    children: [
      { path: 'compose', component: MessageComposeComponent },
      { path: 'inbox', component: MessageInboxComponent },
      { path: 'sent', component: MessageSentComponent },
]
},
  {
    path: 'admin',
    component: AllTemplateBackComponent,
    canActivate: [AuthGuard],  // Protecting the main route
    canActivateChild: [AuthGuard], // Protecting child routes
    data: { roles: ['ADMIN'] },
    children: [
      { path: 'resources', component: ResourceManagementComponent },
      { path: 'resource-reservations', component: ResourceReservationManagementComponent },
      { path: 'resource-maintenance', component: ResourceMaintenanceComponent },
      { path: 'users', component: UserComponent },
      { path: '', redirectTo: 'resources', pathMatch: 'full' },
      { path: 'events', component: GeteventComponent },
      { path: 'create-event', component: CreateeventComponent },
      { path: 'edit-event/:id', component: EditEventComponent },
      { path: 'create-recuitement', component: CreateRecutementComponent },
      { path: 'edit-recruitment/:id', component: EditRecruitmentComponent },
      { path: 'feedback', component: AddFeedbackComponent },
      { path: 'add-feedback/:id', component: AddFeedbackComponent }, // Route pour l'édition
      { path: 'feedback-list', component: FeedbackListComponent },
      { path: 'mental-health', component: MentalHealthComponent },
      { path: 'mental-health-list', component: MentalHealthListComponent },
      { path: '', redirectTo: '/mental-health', pathMatch: 'full' },
      { path: 'resource-reservation-statistics', component: ResourceReservationStatisticsComponent },
      { path: 'maintenance-notifications', component: MaintenanceNotificationAdminComponent },
      {
        path: 'notifications',
        component: NotificationComponent,
      },
      { path: 'documents', component: DocumentManagementComponent },

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
  { path: 'activate-account', component: ActivateAccountComponent },
  { path: 'reset-password-request', component: ResetPasswordRequestComponent },
  { path: 'reset-password', component: ResetPasswordComponent },
  {
    path: 'user',
    component: AllTemplateUserComponent,
    children: [
      { path: 'register', component: RegisterComponent }
    ]
  },

];



@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
