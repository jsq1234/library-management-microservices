import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginEmailComponent } from './components/login-email/login-email.component';
import { SignupComponent } from './components/signup/signup.component';
import { HomeComponent } from './components/home/home.component';
import { ReactiveFormsModule } from '@angular/forms';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { LogoutButtonComponent } from './components/logout-button/logout-button.component';
import { LoginPhonenoComponent } from './components/login-phoneno/login-phoneno.component';
import { LoginContainerComponent } from './components/login-container/login-container.component';
import { SideBarComponent } from './components/side-bar/side-bar.component';
import { BooksComponent } from './components/home/books/books.component';
import { IssuesComponent } from './components/home/issues/issues.component';
import { ConfirmationCodeComponent } from './components/confirmation-code/confirmation-code.component';
import { AuthInterceptor } from './services/auth.interceptor';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginEmailComponent,
    SignupComponent,
    HomeComponent,
    LogoutButtonComponent,
    LoginPhonenoComponent,
    LoginContainerComponent,
    SideBarComponent,
    BooksComponent,
    IssuesComponent,
    ConfirmationCodeComponent,
    ForgotPasswordComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true,
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
