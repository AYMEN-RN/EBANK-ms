import { Component, inject } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AsyncPipe} from '@angular/common';
import {AccountListState, RequestStatus, Account} from '../model/account.model';
import {catchError, map, Observable, of} from 'rxjs';
import {LoadingService} from '../services/loading';

@Component({
  imports: [
    AsyncPipe
  ],
  selector: 'app-accounts',
  styleUrl: './accounts.css',
  templateUrl: './accounts.html',
})
export class Accounts {
  private http = inject(HttpClient);
  public loadingService = inject(LoadingService);
  protected RequestStatus = RequestStatus;
  accounts$ : Observable<AccountListState> = this.http.get<Account[]>
  ("http://localhost:9999/EBANK-SERVICE/accounts")
    .pipe(
      map(response => {
        return {accounts:response, status:RequestStatus.SUCCESS}
      }),
      catchError((err, caught) => {
        return of({status : RequestStatus.ERROR, errorMessage: err.statusText})
      })
    )
}
