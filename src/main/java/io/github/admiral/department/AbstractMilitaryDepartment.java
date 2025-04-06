package io.github.admiral.department;

/** Abstract class for military department, including base method that should implement.*/
public abstract class AbstractMilitaryDepartment {

    /** Get data from other military department. */
    public abstract ExchangeData getData(ExchangeRequest request);
}
