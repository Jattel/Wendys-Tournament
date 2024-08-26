import {Component, EventEmitter, Input, Output} from '@angular/core';
import {TournamentDetailParticipantDto, TournamentStandingsTreeDto} from "../../../../dto/tournament";
import {of} from "rxjs";
import {ToastrService} from "ngx-toastr";
import {HorseSelection} from "../../../../dto/horse";

enum TournamentBranchPosition {
  FINAL_WINNER,
  UPPER,
  LOWER,
}

@Component({
  selector: 'app-tournament-standings-branch',
  templateUrl: './tournament-standings-branch.component.html',
  styleUrls: ['./tournament-standings-branch.component.scss']
})
export class TournamentStandingsBranchComponent {
  protected readonly TournamentBranchPosition = TournamentBranchPosition;
  @Input() branchPosition = TournamentBranchPosition.FINAL_WINNER;
  @Input() treeBranch: TournamentStandingsTreeDto | undefined;
  @Input() allParticipants: TournamentDetailParticipantDto[] = [];
  @Input() winner : TournamentStandingsTreeDto | undefined;

  get isUpperHalf(): boolean {
    return this.branchPosition === TournamentBranchPosition.UPPER;
  }

  get isLowerHalf(): boolean {
    return this.branchPosition === TournamentBranchPosition.LOWER;
  }

  get isFinalWinner(): boolean {
    return this.branchPosition === TournamentBranchPosition.FINAL_WINNER;
  }

  suggestions = (input: string) => {
    // The candidates are either the participants of the previous round matches in this branch
    // or, if this is the first round, all participant horses
    const allCandidates =
      this.treeBranch?.branches?.map(b => b.thisParticipant)
      ?? this.allParticipants;
    if(allCandidates[0] === null || allCandidates[1] === null){
      return of([]);
    }
    const results = allCandidates
        .filter(x => !!x)
        .map(x => <TournamentDetailParticipantDto><unknown>x)
        .filter((x) =>
            x.name.toUpperCase().match(new RegExp(`.*${input.toUpperCase()}.*`)));
    return of(results);
  };

  public formatParticipant(participant: TournamentDetailParticipantDto | null): string {
    return participant
        ? `${participant.name} (${new Date(participant.dateOfBirth).toLocaleDateString()})`
        : "";
  }

  disabled() : boolean {
    if(this.winner?.thisParticipant){
      return true;
    } else {
      return false;
    }
  }
}
