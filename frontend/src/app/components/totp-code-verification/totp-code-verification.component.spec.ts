import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TotpCodeVerificationComponent } from './totp-code-verification.component';

describe('TotpCodeVerificationComponent', () => {
  let component: TotpCodeVerificationComponent;
  let fixture: ComponentFixture<TotpCodeVerificationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TotpCodeVerificationComponent]
    });
    fixture = TestBed.createComponent(TotpCodeVerificationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
