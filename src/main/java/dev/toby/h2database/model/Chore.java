package dev.toby.h2database.model;

import org.springframework.data.annotation.Id;

public record Chore(@Id Integer Id, String Username, String Flat, String Chore, Integer Chorescompleted) {
}
