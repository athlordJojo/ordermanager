import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AudioService {

  private bellSound;

  constructor() {
    this.bellSound = new Audio("../assets/doorbell.mp3");
    this.bellSound.load();
    this.bellSound.autoplay = false;
    this.bellSound.muted = true
  }

  playSound(scoreboardNumber:string){
    // speechSynthesis.speak(msg);

    // var msg = new SpeechSynthesisUtterance();
    // msg.text = 'Nummer ' + scoreboardNumber + " bitte";
    // msg.lang = 'de-DE';
    // msg.volume = 1; // 0 to 1
    // msg.rate = 1; // 0.1 to 10
    // msg.pitch = 2; //0 to 2
    this.bellSound.muted = false;
    this.bellSound.play();
  }
}
