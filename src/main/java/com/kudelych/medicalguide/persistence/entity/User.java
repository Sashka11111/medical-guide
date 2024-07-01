package com.kudelych.medicalguide.persistence.entity;

public record User(
    int id,
    String username,
    String password,
    UserRole role)
    implements Entity, Comparable<User> {

  @Override
  public int compareTo(User o) {
    return this.username.compareTo(o.username);
  }
}

