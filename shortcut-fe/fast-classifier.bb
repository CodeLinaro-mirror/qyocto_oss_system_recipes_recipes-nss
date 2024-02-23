DESCRIPTION = "Shortcut forward engine(SFE) fast classifier"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module

CLEANBROKEN = "1"

FILESPATH =+ "${TOPDIR}/../opensource/:"


SRC_URI = "file://shortcut-fe/ \
	   "
DEPENDS = "virtual/kernel"

S = "${WORKDIR}/shortcut-fe/fast-classifier"

PACKAGES += "kernel-module-fast-classifier"

EXTRA_OEMAKE += "TOOL_PATH='${STAGING_BINDIR_TOOLCHAIN}' \
		SYS_PATH='${STAGING_KERNEL_BUILDDIR}' \
		TOOLPREFIX='${TARGET_PREFIX}' \
		KVER='${KERNEL_VERSION}' \
		ARCH='arm' -j1 \
		"
EXTRA_CFLAGS += "-DSFE_SUPPORT_IPV6"

do_compile() {
	make -C  "${STAGING_KERNEL_BUILDDIR}" \
		CROSS_COMPILE="${TARGET_PREFIX}" \
		ARCH="arm" \
		SUBDIRS="${S}" \
		EXTRA_CFLAGS="${EXTRA_CFLAGS}" \
		SFE_SUPPORT_IPV6=1 \
		modules
}

do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 fast-classifier${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}/${includedir}/shortcut-fe
	install -m 0644 ${WORKDIR}/shortcut-fe/fast-classifier/fast-classifier.h ${D}/${includedir}/shortcut-fe/
}
