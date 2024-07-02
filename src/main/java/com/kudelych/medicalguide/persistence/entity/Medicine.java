package com.kudelych.medicalguide.persistence.entity;

public record Medicine(
    int id,
    String name,
    String description,
    String manufacturer,
    String form,
    String purpose,
    byte[] image) implements Comparable<Medicine> {

  @Override
  public int compareTo(Medicine o) {
    return Integer.compare(this.id, o.id);
  }
}
