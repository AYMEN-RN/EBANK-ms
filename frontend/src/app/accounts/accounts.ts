import { Component, inject } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AsyncPipe} from '@angular/common';

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
  accounts$ = this.http.get<any[]>("http://localhost:9999/EBANK-SERVICE/accounts")
}
