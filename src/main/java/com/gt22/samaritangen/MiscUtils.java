package com.gt22.samaritangen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.*;

public class MiscUtils {


	public static class JoinUtils {

		public static BinaryOperator<String> join(String delimiter) {
			return (s1, s2) -> s1 + delimiter + s2;
		}

		public static <T> Optional<String> join(Collection<T> coll, Function<T, String> mapper, String delimiter) {
			return coll.stream().map(mapper).reduce(join(delimiter));
		}

		public static Optional<String> join(Collection<String> coll, String delimiter) {
			return coll.stream().reduce(join(delimiter));
		}

		public static <T> Optional<String> join(T[] arr, Function<T, String> mapper, String delimiter) {
			return Arrays.stream(arr).map(mapper).reduce(join(delimiter));
		}

		public static Optional<String> join(String[] arr, String delimiter) {
			return Arrays.stream(arr).reduce(join(delimiter));
		}
	}

	public static class ArrayUtils {

		public static <T> void forEach(T[] arr, Consumer<T> action) {
			forEach(arr, (e, i) -> action.accept(e));
		}

		public static <T> void forEach(T[] arr, BiConsumer<T, Integer> action) {
			for(int i = 0; i < arr.length; i++) {
				action.accept(arr[i], i);
			}
		}

		public static <I, O> O[] map(I[] arr, Function<I, O> mapper, IntFunction<O[]> arrayCreator) {
			return map(arr, (e, i) -> mapper.apply(e), arrayCreator);
		}

		public static <I, O> O[] map(I[] arr, BiFunction<I, Integer, O> mapper, IntFunction<O[]> arrayCreator) {
			O[] out = arrayCreator.apply(arr.length);
			forEach(arr, (e, i) -> out[i] = mapper.apply(e, i));
			return out;
		}

	}

	public static Path getRandomFile(Path dir) throws IOException {
		while(dir != null && Files.isDirectory(dir)) {
			dir = Files.list(dir).findAny().orElse(null);
		}
		return dir;
	}
}
