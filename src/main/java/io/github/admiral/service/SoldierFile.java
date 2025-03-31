package io.github.admiral.service;

import lombok.Getter;

/**
 * Abstract class of Service, the following field is needed.
 * Child class can define custom field.
 *
 * @author Jiahao Hwang
 */
@Getter
public abstract class SoldierFile {
    protected String name;
    protected String ip;
    protected int port;
    protected String[] consumes;
    protected String produce;
}
