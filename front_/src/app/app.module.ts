import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from './auth/auth.interceptor';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterModule } from '@angular/router';
import { BrowserAnimationsModule, provideAnimations } from '@angular/platform-browser/animations';
import { ToastrModule, provideToastr } from 'ngx-toastr';
import { CodeInputModule } from 'angular-code-input';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AllTemplateBackComponent } from './BackOffice/all-template-back/all-template-back.component';
import { AllTemplateFrontComponent } from './FrontOffice/all-template-front/all-template-front.component';
import { HeaderFrontComponent } from './FrontOffice/header-front/header-front.component';
import { FooterFrontComponent } from './FrontOffice/footer-front/footer-front.component';
import { SidebarBackComponent } from './BackOffice/sidebar-back/sidebar-back.component';
import { NavbarBackComponent } from './BackOffice/navbar-back/navbar-back.component';
import { FooterBackComponent } from './BackOffice/footer-back/footer-back.component';
import { HomeFrontComponent } from './FrontOffice/home-front/home-front.component';
import { AboutFrontComponent } from './FrontOffice/about-front/about-front.component';
import { ServiceFrontComponent } from './FrontOffice/service-front/service-front.component';
import { RegisterComponent } from './User/register/register.component';
import { AllTemplateUserComponent } from './User/all-template-user/all-template-user.component';
import { LoginComponent } from './User/login/login.component';
import { PasswordComponent } from './User/password/password.component';
import { PortfolioComponent } from './FrontOffice/portfolio/portfolio.component';
import { ContactComponent } from './FrontOffice/contact/contact.component';
import { ResourceManagementComponent } from './BackOffice/resource-management/resource-management.component';
import { ResourceReservationManagementComponent } from './BackOffice/resource-reservation-management/resource-reservation-management.component';
import { ResourceMaintenanceComponent } from './BackOffice/resource-maintenance/resource-maintenance.component';
import { ReservationService } from './services/reservation.service';
import { ResourceService } from './services/resource.service';
import { AddInteractivePublicationComponent } from './FrontOffice/add-interactive-publication/add-interactive-publication.component';
import { ActivateAccountComponent } from './User/activate-account/activate-account.component';
import { ResetPasswordRequestComponent } from './User/reset-password-request/reset-password-request.component';
import { ResetPasswordComponent } from './User/reset-password/reset-password.component';
import { UserComponent } from './BackOffice/user/user.component';
import { ProfileComponent } from './BackOffice/profile/profile.component';
import { CreateeventComponent } from './models/event/createevent/createevent/createevent.component';
import { GeteventComponent } from './models/event/getevent/getevent/getevent.component';
import { EditEventComponent } from './models/event/edit-event/edit-event.component';
import { CreateRecutementComponent } from './models/recutement/create-recutement/create-recutement.component';
import { MonitoringRecruitmentListComponent } from './models/recutement/monitoring-recruitment-list/monitoring-recruitment-list.component';
import { EditRecruitmentComponent } from './models/recutement/edit-recruitment/edit-recruitment.component';
import { AddFeedbackComponent } from './BackOffice/add-feedback/add-feedback.component';
import { MentalHealthComponent } from './BackOffice/mental-health/mental-health.component';
import { DisableDatesDirective } from './directives/disable-dates.directive';
import { ResourceReservationStatisticsComponent } from './BackOffice/resource-reservation-statistics/resource-reservation-statistics.component';
import { MaintenanceNotificationAdminComponent } from './components/maintenance-notification-admin/maintenance-notification-admin.component';
import { NotificationBadgeComponent } from './components/notification-badge/notification-badge.component';
import { NotificationComponent } from './components/notification/notification.component';
import { DocumentManagementComponent } from './BackOffice/document-management/document-management.component';
import { DocumentService } from './services/document.service';
import { DocumentFilterPipe } from './filters/document-filter.pipe';
import { RecommendationService } from './services/RecommendationService.service';
import { MessageComposeComponent } from './FrontOffice/message-compose/message-compose.component';
import { MessageInboxComponent } from './FrontOffice/message-inbox/message-inbox.component';
import { MessageSentComponent } from './FrontOffice/message-sent/message-sent.component';
import { FeedbackListComponent } from './BackOffice/feedback-list/feedback-list.component';
import { MentalHealthListComponent } from './BackOffice/mental-health-list/mental-health-list.component';
import { NgChartsModule } from 'ng2-charts';
import { MatTableModule } from '@angular/material/table';

@NgModule({
  declarations: [
    AppComponent,
    AllTemplateBackComponent,
    AllTemplateFrontComponent,
    HeaderFrontComponent,
    FooterFrontComponent,
    SidebarBackComponent,
    NavbarBackComponent,
    FooterBackComponent,
    HomeFrontComponent,
    AboutFrontComponent,
    ServiceFrontComponent,
    RegisterComponent,
    AllTemplateUserComponent,
    LoginComponent,
    PasswordComponent,
    PortfolioComponent,
    ContactComponent,
    ResourceManagementComponent,
    ActivateAccountComponent,
    ResetPasswordRequestComponent,
    ResetPasswordComponent,
    ResourceReservationManagementComponent,
    ResourceMaintenanceComponent,
    AddInteractivePublicationComponent,
    UserComponent,
    ProfileComponent,
    CreateeventComponent, // Kept here, but verify it’s not declared elsewhere
    GeteventComponent,
    EditEventComponent,
    CreateRecutementComponent,
    MonitoringRecruitmentListComponent,
    EditRecruitmentComponent,
    AddFeedbackComponent,
    MentalHealthComponent,
    DisableDatesDirective,
    ResourceReservationStatisticsComponent,
    NotificationComponent,
    NotificationBadgeComponent,
    MaintenanceNotificationAdminComponent,
    DocumentManagementComponent,
    DocumentFilterPipe,
    MessageComposeComponent,
    MessageInboxComponent,
    MessageSentComponent,
    FeedbackListComponent,
    MentalHealthListComponent,
  ],
  imports: [
    BrowserModule,
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    HttpClientModule,
    RouterModule,
    CodeInputModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    ToastrModule.forRoot({
      timeOut: 3000,
      positionClass: 'toast-top-right',
      preventDuplicates: true,
    }),
    NgChartsModule,
    MatTableModule,
  ],
  providers: [
    ReservationService,
    ResourceService,
    DocumentService,
    RecommendationService,
    DatePipe,
    provideAnimations(),
    provideToastr(),
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true,
    },
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  bootstrap: [AppComponent],
})
export class AppModule {}
