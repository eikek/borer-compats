# borer compats

This is a scala (3) library to provide some compat code for
[borer](https://github.com/sirthias/borer).

The `fs2` module provides a decoder for `Stream[F, Byte]` decoding
consecutive occurrences of `A`.

The `http4s` module provides instances of `EntityEncoder` and
`EntityDecoder` for http4s utilising
[borer](https://github.com/sirthias/borer) for decoding and encoding
json and cbor.
