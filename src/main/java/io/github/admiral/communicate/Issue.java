package io.github.admiral.communicate;

import lombok.RequiredArgsConstructor;

/** A class that issue soldiers to do.*/
@RequiredArgsConstructor
public class Issue {
    /** The command type that needs to finish.*/
    private final Command command;

    /** The soldier name that needed to finish the command.*/
    private final String name;

    /** The ip address and port that the need to get requisite data. */
    private final String[] infoStations;

    /** Job id the command belongs to.*/
    private final String jobId;
}
