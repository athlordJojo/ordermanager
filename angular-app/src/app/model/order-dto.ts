export class OrderDto {
  uuid: string;
  title: string;
  scoreBoardNumber: number;
  state: OrderState;
  createdDate: Date;
  lastModifiedDate: Date;
}

export enum OrderState {
  IN_PROGRESS= "IN_PROGRESS",
  READY= "READY"
}
