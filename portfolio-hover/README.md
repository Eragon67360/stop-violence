# portfolio-hover

Source of the hover preview shown on thomasmoserdev.com/projects for Stop'Violence.

The app has no live build to capture, so the loop animates the portfolio card image
(`captures/card.png`: the login, home and contacts screens) and the app's own resources:

- The zoomed home screen is `app/src/main/res/drawable/font_accueil.jpeg`, aligned on the card's DANGER button.
- The press follows `res/layout/activity_localisation.xml` (180dp `#926aa6` DANGER button) and
  `Localisation.java`: DANGER sends the SMS to the trusted contacts and shows the "SMS sent." toast.
- The camera then glides to the contacts screen and back to the card frame, so the loop is seamless.

Files:

- `comp.html`: the 8s, 1280x800 loop (every frame is a function of time).
- `captures/`: the card image and Roboto (Apache 2.0) for the toast text.
- `out/`: rendered `stopviolence.mp4` (silent H.264) and its first frame.

`engine.js`, `base.css` and `render.mjs` are copied from the portfolio's `resources/hover-videos/kit`,
which documents how to render (`HOVER_KIT_DEPS=<deps>/package.json node render.mjs comp.html out/stopviolence`).
