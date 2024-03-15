import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SignupComponent } from './components/signup/signup.component';
import { HomeComponent } from './components/home/home.component';
import { userGuard } from './guards/user.guard';
import { authGuard } from './guards/auth.guard';
import { LoginContainerComponent } from './components/login-container/login-container.component';
import { ConfirmationCodeComponent } from './components/confirmation-code/confirmation-code.component';
import { LoginEmailComponent } from './components/login-email/login-email.component';
import { LoginPhonenoComponent } from './components/login-phoneno/login-phoneno.component';
import { BooksComponent } from './components/home/books/books.component';
import { IssuesComponent } from './components/home/issues/issues.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';

const routes: Routes = [
  {
    path: 'login',
    component: LoginContainerComponent,
    canActivate: [userGuard],
  },
  {
    path: 'login/email',
    component: LoginEmailComponent,
    canActivate: [userGuard],
  },
  {
    path: 'login/phoneno',
    component: LoginPhonenoComponent,
    canActivate: [userGuard],
  },
  {
    path: 'forgot_password',
    component: ForgotPasswordComponent,
    canActivate: [userGuard],
  },
  {
    path: 'signup',
    component: SignupComponent,
    canActivate: [userGuard],
  },
  {
    path: 'confirm/:userId',
    component: ConfirmationCodeComponent,
    canActivate: [userGuard],
  },
  {
    path: 'home',
    component: HomeComponent,
    canActivate: [authGuard],
    children: [
      {
        path: 'books',
        component: BooksComponent,
      },
      {
        path: 'issues',
        component: IssuesComponent,
      },
    ],
  },
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full',
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
