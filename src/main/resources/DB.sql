CREATE TABLE role (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE appuser (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE appuser_roles (
    appuser_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL
);
ALTER TABLE appuser_roles ADD CONSTRAINT appuser_roles_pkey PRIMARY KEY(appuser_id, role_id);
ALTER TABLE appuser_roles ADD CONSTRAINT appuser_roles_user_fkey FOREIGN KEY(appuser_id) REFERENCES appuser(id);
ALTER TABLE appuser_roles ADD CONSTRAINT appuser_roles_role_fkey FOREIGN KEY(role_id) REFERENCES role(id);