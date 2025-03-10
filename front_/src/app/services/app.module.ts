import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { AuthInterceptor } from './auth/auth.interceptor';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule,ReactiveFormsModule   } from '@angular/forms';

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
import { RegisterComponent } from './models/User/register/register.component';
import { AllTemplateUserComponent } from './models/User/all-template-user/all-template-user.component';
import { LoginComponent } from './models/User/login/login.component';
import { PasswordComponent } from './models/User/password/password.component';
import { PortfolioComponent } from './FrontOffice/portfolio/portfolio.component';
import { ContactComponent } from './FrontOffice/contact/contact.component';
import { ResourceManagementComponent } from './BackOffice/resource-management/resource-management.component';
import { ResourceReservationManagementComponent} from "./BackOffice/resource-reservation-management/resource-reservation-management.component";
import { ReservationService } from './services/reservation.service';  // Importation du service
import { ResourceService} from "./services/resource.service";
import { AddInteractivePublicationComponent } from './FrontOffice/add-interactive-publication/add-interactive-publication.component';
import { ToastrModule } from 'ngx-toastr';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CreateeventComponent } from './models/event/createevent/createevent/createevent.component';
import { GeteventComponent } from './models/event/getevent/getevent/getevent.component';
import { EditEventComponent } from './models/event/edit-event/edit-event.component';
import { CreateRecutementComponent } from './models/recutement/create-recutement/create-recutement.component';
import { MonitoringRecruitmentListComponent } from './models/recutement/monitoring-recruitment-list/monitoring-recruitment-list.component';
import { EditRecruitmentComponent } from './models/recutement/edit-recruitment/edit-recruitment.component';
import { DashboardComponent } from './FrontOffice/dashboard/dashboard.component';



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
    ResourceReservationManagementComponent,
    AddInteractivePublicationComponent, // Déclaration des composants
    CreateeventComponent,
    GeteventComponent,
    EditEventComponent,
    CreateRecutementComponent,
    MonitoringRecruitmentListComponent,
    EditRecruitmentComponent,
    DashboardComponent  
  ],
  imports: [
    FormsModule,
    HttpClientModule,
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    ToastrModule.forRoot({
      timeOut: 3000,
      positionClass: 'toast-top-right',
      preventDuplicates: true,
    })
,
  ],
  providers: [
     ReservationService,
    ResourceService,
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
