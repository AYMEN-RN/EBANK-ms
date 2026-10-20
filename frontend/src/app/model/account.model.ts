export interface Account {
    id?: number;
    balance?: number,
    createdAt?: string,
    accType?: string,
    customerId?: number,
  }
export enum RequestStatus {
  SUCCESS = 0,
  ERROR = 1,
}

  export interface AccountListState{
    accounts?: Account[],
    status?: RequestStatus,
    errorMessage?: string,
  }
