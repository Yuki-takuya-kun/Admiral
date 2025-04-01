package io.github.admiral.hr;

import lombok.Getter;

/**
 * Abstract class of Service, the following field is needed.
 * Child class can define custom field.
 *
 * @author Jiahao Hwang
 */
@Getter
public abstract class SoldierFile {
    protected Troop troop;
    protected String ip;
    protected int port;
}
