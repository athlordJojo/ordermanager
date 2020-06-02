import {Injectable} from '@angular/core';

enum MODE {
  OFF,
  SOUND,
  SPEAK
}

@Injectable({
  providedIn: 'root'
})
export class AudioService {

  private sound;
  private readonly spechSynthesis;
  private readonly currentMode: MODE;

  constructor() {
    this.sound = new Audio("../assets/doorbell.mp3");
    this.sound.load();
    this.sound.autoplay = false;
    this.sound.muted = true

    this.spechSynthesis = new SpeechSynthesisUtterance();
    this.spechSynthesis.lang = 'de-DE';
    this.spechSynthesis.volume = 1; // 0 to 1
    this.spechSynthesis.rate = 1; // 0.1 to 10
    this.spechSynthesis.pitch = 2; //0 to 2

    this.currentMode = MODE.SOUND;
  }

  playSound(scoreboardNumber: string) {
    if (MODE.SOUND == this.currentMode) {
      this.sound.muted = false;
      this.sound.play();
    } else if (MODE.SPEAK == this.currentMode) {
      this.spechSynthesis.text = 'Nummer ' + scoreboardNumber + 'bitte';
      speechSynthesis.speak(this.spechSynthesis);
    }
  }
}
